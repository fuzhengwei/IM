package org.itstack.naive.chat.ui.view.chat;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.itstack.naive.chat.ui.view.chat.data.TalkBoxData;
import org.itstack.naive.chat.ui.view.face.FaceController;

import java.util.Date;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 小傅哥 on @2020
 * <p>
 * 事件定义
 */
public class ChatEventDefine {

    private ChatInit chatInit;
    private IChatEvent chatEvent;
    private IChatMethod chatMethod;

    public ChatEventDefine(ChatInit chatInit, IChatEvent chatEvent, IChatMethod chatMethod) {
        this.chatInit = chatInit;
        this.chatEvent = chatEvent;
        this.chatMethod = chatMethod;

        chatInit.move();
        min();               // 最小化
        quit();              // 退出
        barChat();           // 聊天
        barFriend();         // 好友
        doEventTextSend();   // 发送消息事件[键盘]
        doEventTouchSend();  // 发送消息事件[按钮]
        doEventToolFace();   // 表情窗体
    }

    // 最小化
    private void min() {
        chatInit.$("group_bar_chat_min", Button.class).setOnAction(event -> {
            chatInit.setIconified(true);
        });
        chatInit.$("group_bar_friend_min", Button.class).setOnAction(event -> {
            chatInit.setIconified(true);
        });
    }

    // 退出
    private void quit() {
        chatInit.$("group_bar_chat_close", Button.class).setOnAction(event -> {
            chatEvent.doQuit();
            chatInit.close();
            System.exit(0);
        });
        chatInit.$("group_bar_friend_close", Button.class).setOnAction(event -> {
            chatEvent.doQuit();
            chatInit.close();
            System.exit(0);
        });
    }

    // 聊天
    private void barChat() {
        Button bar_chat = chatInit.$("bar_chat", Button.class);
        Pane group_bar_chat = chatInit.$("group_bar_chat", Pane.class);
        bar_chat.setOnAction(event -> {
            switchBarChat(bar_chat, group_bar_chat, true);
            switchBarFriend(chatInit.$("bar_friend", Button.class), chatInit.$("group_bar_friend", Pane.class), false);
        });
        bar_chat.setOnMouseEntered(event -> {
            boolean visible = group_bar_chat.isVisible();
            if (visible) return;
            bar_chat.setStyle("-fx-background-image: url('/fxml/chat/img/system/chat_1.png')");
        });
        bar_chat.setOnMouseExited(event -> {
            boolean visible = group_bar_chat.isVisible();
            if (visible) return;
            bar_chat.setStyle("-fx-background-image: url('/fxml/chat/img/system/chat_0.png')");
        });
    }

    // 好友
    private void barFriend() {
        Button bar_friend = chatInit.$("bar_friend", Button.class);
        Pane group_bar_friend = chatInit.$("group_bar_friend", Pane.class);
        bar_friend.setOnAction(event -> {
            switchBarChat(chatInit.$("bar_chat", Button.class), chatInit.$("group_bar_chat", Pane.class), false);
            switchBarFriend(bar_friend, group_bar_friend, true);
        });
        bar_friend.setOnMouseEntered(event -> {
            boolean visible = group_bar_friend.isVisible();
            if (visible) return;
            bar_friend.setStyle("-fx-background-image: url('/fxml/chat/img/system/friend_1.png')");
        });
        bar_friend.setOnMouseExited(event -> {
            boolean visible = group_bar_friend.isVisible();
            if (visible) return;
            bar_friend.setStyle("-fx-background-image: url('/fxml/chat/img/system/friend_0.png')");
        });
    }

    // 切换：bar_chat
    private void switchBarChat(Button bar_chat, Pane group_bar_chat, boolean toggle) {
        if (toggle) {
            bar_chat.setStyle("-fx-background-image: url('/fxml/chat/img/system/chat_2.png')");
            group_bar_chat.setVisible(true);
        } else {
            bar_chat.setStyle("-fx-background-image: url('/fxml/chat/img/system/chat_0.png')");
            group_bar_chat.setVisible(false);
        }
    }

    // 切换：bar_friend
    private void switchBarFriend(Button bar_friend, Pane group_bar_friend, boolean toggle) {
        if (toggle) {
            bar_friend.setStyle("-fx-background-image: url('/fxml/chat/img/system/friend_2.png')");
            group_bar_friend.setVisible(true);
        } else {
            bar_friend.setStyle("-fx-background-image: url('/fxml/chat/img/system/friend_0.png')");
            group_bar_friend.setVisible(false);
        }
    }

    // 好友；开启与好友发送消息 [点击发送消息时候触发 -> 添加到对话框、选中、展示对话列表]
    public void doEventOpenFriendUserSendMsg(Button sendMsgButton, String userFriendId, String userFriendNickName, String userFriendHead) {
        sendMsgButton.setOnAction(event -> {
            // 1. 添加好友到对话框
            chatMethod.addTalkBox(0, 0, userFriendId, userFriendNickName, userFriendHead, null, null, true);
            // 2. 切换到对话框窗口
            switchBarChat(chatInit.$("bar_chat", Button.class), chatInit.$("group_bar_chat", Pane.class), true);
            switchBarFriend(chatInit.$("bar_friend", Button.class), chatInit.$("group_bar_friend", Pane.class), false);
            // 3. 事件处理；填充到对话框
            chatEvent.doEventAddTalkUser(chatInit.userId, userFriendId);
        });
    }

    // 群组；开启与群组发送消息
    public void doEventOpenFriendGroupSendMsg(Button sendMsgButton, String groupId, String groupName, String groupHead) {
        sendMsgButton.setOnAction(event -> {
            // 1. 添加好友到对话框
            chatMethod.addTalkBox(0, 1, groupId, groupName, groupHead, null, null, true);
            // 2. 切换到对话框窗口
            switchBarChat(chatInit.$("bar_chat", Button.class), chatInit.$("group_bar_chat", Pane.class), true);
            switchBarFriend(chatInit.$("bar_friend", Button.class), chatInit.$("group_bar_friend", Pane.class), false);
            // 3. 事件处理；填充到对话框
            chatEvent.doEventAddTalkGroup(chatInit.userId, groupId);
        });
    }

    // 发送消息事件[键盘]
    private void doEventTextSend() {
        TextArea txt_input = chatInit.$("txt_input", TextArea.class);
        txt_input.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                doEventSendMsg();
            }
        });
    }

    // 发送消息事件[按钮]
    private void doEventTouchSend() {
        Label touch_send = chatInit.$("touch_send", Label.class);
        touch_send.setOnMousePressed(event -> {
            doEventSendMsg();
        });
    }

    private void doEventSendMsg() {
        TextArea txt_input = chatInit.$("txt_input", TextArea.class);
        MultipleSelectionModel selectionModel = chatInit.$("talkList", ListView.class).getSelectionModel();
        Pane selectedItem = (Pane) selectionModel.getSelectedItem();
        // 对话信息
        TalkBoxData talkBoxData = (TalkBoxData) selectedItem.getUserData();
        String msg = txt_input.getText();
        if (null == msg || "".equals(msg) || "".equals(msg.trim())) {
            return;
        }
        Date msgDate = new Date();
        // 发送消息
        chatEvent.doSendMsg(chatInit.userId, talkBoxData.getTalkId(), talkBoxData.getTalkType(), msg, 0, msgDate);
        // 发送事件给自己添加消息
        chatMethod.addTalkMsgRight(talkBoxData.getTalkId(), msg, 0, msgDate, true, true, false);
        txt_input.clear();
    }

    // 表情
    private void doEventToolFace() {
        FaceController face = new FaceController(chatInit, chatInit, chatEvent, chatMethod);
        Button tool_face = chatInit.$("tool_face", Button.class);
        tool_face.setOnMousePressed(event -> {
            face.doShowFace(chatMethod.getToolFaceX(), chatMethod.getToolFaceY());
        });
    }

}
