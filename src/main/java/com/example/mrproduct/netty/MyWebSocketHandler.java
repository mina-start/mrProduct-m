package com.example.mrproduct.netty;

import com.example.mrproduct.proto.WebSocketInfo;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Configuration
public class MyWebSocketHandler extends SimpleChannelInboundHandler<WebSocketInfo.msgMessage> {
    private static Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static Map<String, List<ChannelHandlerContext>> cmap = new LinkedHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与客户端建立连接，通道开启！");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与客户端断开连接，通道关闭！");
    }

    /**
     * 读取发送的消息
     *
     * @param ctx 通道上下文
     * @param msg 信息内容
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketInfo.msgMessage msg) throws Exception {
        String type = msg.getType();
        logger.info("type: " + type);
        switch (type) {
            case "1": //登录
                websocketLogin(msg.getUser(), ctx);
                break;
            case "2"://2-指令消息
                blueToothReceive(msg);
                break;
            case "3"://3-经纬度消息
                lonAndLatReceive(msg);
                break;
            case "4"://4-其他消息
                msgReceive(msg);
                break;
            case "5"://退出
                websocketLogout(msg.getUser(), ctx);
                break;
        }
        logger.info(String.format("收到客户端%s的数据：%s", ctx.channel().id(), msg.getMessage()));
    }

    private void msgReceive(WebSocketInfo.msgMessage msg) {
        String user = msg.getUser();
        String message = msg.getMessage();
        logger.info("send user: {%s}, msg: {%s}", user, message);
    }

    private void lonAndLatReceive(WebSocketInfo.msgMessage msg) {
        String user = msg.getUser();
        String message = msg.getMessage();
        logger.info("send user: {%s}, lonAndLat msg: {%s}", user, message);
    }

    private void blueToothReceive(WebSocketInfo.msgMessage msg) {
        String user = msg.getUser();
        String message = msg.getMessage();
        logger.info("send user: {%s}, blueTooth msg: {%s}", user, message);
    }

    private void websocketLogout(String user, ChannelHandlerContext ctx) {
        if (cmap.containsKey(user)) {
            List<ChannelHandlerContext> contextList = cmap.get(user);
            if (contextList.contains(ctx)) {
                contextList.remove(ctx);
                ctx.close();
            }

            if (contextList.isEmpty())
                cmap.remove(user);
        }
        logger.info("uid: {%s} 退出登录", user);
    }

    private void websocketLogin(String user, ChannelHandlerContext ctx) {
        List<ChannelHandlerContext> contextList = cmap.get(user);
        if (contextList == null) {
            contextList = new LinkedList<>();
        }
        contextList.add(ctx);
        cmap.put(user, contextList);
        logger.info(user + "登录");
    }

    public static Map<String, byte[]> byteMap = new LinkedHashMap<>();

    /**
     * 发送消息
     *
     * @param user 接收方
     * @param type 信息type
     * @param msg  信息msg
     *             额外信息
     */
    public void sendMessage(String user, String type, String msg) {
        sendMessage(user, type, msg, null);
    }

    public void sendMessage(String user, String type, String msg, String otherMsg) {
        sendMessage(user, type, msg, otherMsg, null);
    }

    public void sendMessage(String user, String type, String msg, String otherMsg, byte[] msgBytes) {
        WebSocketInfo.msgMessage.Builder msgInfoBuild = getMsgInfoBuild(user, type, msg, otherMsg, msgBytes);
        cmap.get(user).forEach(ctx -> ctx.writeAndFlush(msgInfoBuild));
    }

    private WebSocketInfo.msgMessage.Builder getMsgInfoBuild(String user, String type, String msg, String otherMsg, byte[] msgBytes) {
        return WebSocketInfo.msgMessage.newBuilder().setType(type).setUser(user).setMessage(msg).setOtherMsg(otherMsg).setOtherMsgByte(ByteString.copyFrom(msgBytes));
    }

}
