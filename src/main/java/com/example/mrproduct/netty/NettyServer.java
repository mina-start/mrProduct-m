package com.example.mrproduct.netty;

import com.example.mrproduct.proto.WebSocketInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

@Configuration
public class NettyServer {
    private static Logger log = LoggerFactory.getLogger(NettyServer.class);

    @Resource
    private MyWebSocketHandler myWebSocketHandler;

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 1024 * 1024 * 10);
            sb.group(group, bossGroup) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(8081)// 绑定监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                            ch.pipeline()
                                    .addLast(new HttpServerCodec())
                                    //以块的方式来写的处理器，支持大数据流写入
                                    .addLast(new ChunkedWriteHandler())
                                    //支持参数对象解析，比如post参数，设置聚合内容的最大长度
                                    .addLast(new HttpObjectAggregator(1024 * 1024 * 10))
                                    //支持websocket数据压缩
                                    .addLast(new WebSocketServerCompressionHandler())
                                    .addLast(new WebSocketServerProtocolHandler("/webSocket", "WebSocket", true, 65536 * 10))
                                    //解码器，通过Google Protocol Buffers序列化框架动态的切割接收到的ByteBuf
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    //Google Protocol Buffers 长度属性编码器
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    // 协议包解码
                                    .addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                                        @Override
                                        protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                                            // log.info("received client msg ------------------------");
                                            if (frame instanceof TextWebSocketFrame) {
                                                // 文本消息
                                                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                                                //log.info("MsgType is TextWebSocketFrame");
                                            } else if (frame instanceof BinaryWebSocketFrame) {
                                                // 二进制消息
                                                ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
                                                objs.add(buf);
                                                // 自旋累加
                                                buf.retain();
                                                //log.info("MsgType is BinaryWebSocketFrame");
                                            } else if (frame instanceof PongWebSocketFrame) {
                                                // PING存活检测消息
                                                //log.info("MsgType is PongWebSocketFrame ");
                                            } else if (frame instanceof CloseWebSocketFrame) {
                                                // 关闭指令消息
                                                //log.info("MsgType is CloseWebSocketFrame");
                                                ch.close();
                                            }

                                        }
                                    })
                                    // 协议包编码
                                    .addLast(new MessageToMessageEncoder<ByteBuf>() {
                                        @Override
                                        protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
                                            // 将Protbuf消息包装成Binary Frame 消息
                                            WebSocketFrame frame = new BinaryWebSocketFrame(msg);
                                            out.add(frame);
                                            frame.retain();
                                            //log.info(msg + "编码成功");
                                        }
                                    })
                                    // 按约定的ResponseMessage对象，进行解码
                                    .addLast(new ProtobufDecoder(WebSocketInfo.msgMessage.getDefaultInstance()))
                                    // Protocol Buffers 编码器
                                    .addLast(new ProtobufEncoder())
                                    // 自定义数据接收处理器
                                    .addLast(myWebSocketHandler);
                        }
                    });
            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
            System.out.println(NettyServer.class + "已启动，正在监听： " + cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
            bossGroup.shutdownGracefully().sync();
        }
    }
}
