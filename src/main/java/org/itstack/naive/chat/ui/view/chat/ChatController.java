package org.itstack.naive.chat.ui.view.chat;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import org.itstack.naive.chat.ui.util.CacheUtil;
import org.itstack.naive.chat.ui.util.Ids;
import org.itstack.naive.chat.ui.view.chat.data.TalkData;
import org.itstack.naive.chat.ui.view.chat.data.GroupsData;
import org.itstack.naive.chat.ui.view.chat.data.RemindCount;
import org.itstack.naive.chat.ui.view.chat.element.group_bar_chat.ElementInfoBox;
import org.itstack.naive.chat.ui.view.chat.element.group_bar_chat.ElementTalk;
import org.itstack.naive.chat.ui.view.chat.element.group_bar_friend.ElementFriendGroup;
import org.itstack.naive.chat.ui.view.chat.element.group_bar_friend.ElementFriendLuckUser;
import org.itstack.naive.chat.ui.view.chat.element.group_bar_friend.ElementFriendUser;

import java.util.Date;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 小傅哥 on @2020
 */
public class ChatController extends ChatInit implements IChatMethod {

    private ChatView chatView;
    private ChatEventDefine chatEventDefine;

    public ChatController(IChatEvent chatEvent) {
        super(chatEvent);
    }

    @Override
    public void initView() {
        chatView = new ChatView(this, chatEvent);
    }

    @Override
    public void initEventDefine() {
        chatEventDefine = new ChatEventDefine(this, chatEvent, this);
    }

    @Override
    public void doShow() {
        super.show();
    }

    @Override
    public void setUserInfo(String userId, String userNickName, String userHead) {
        super.userId = userId;
        super.userNickName = userNickName;
        super.userHead = userHead;
        Button button = $("bar_portrait", Button.class);
        button.setStyle(String.format("-fx-background-image: url('/fxml/chat/img/head/%s.png')", userHead));
    }

    @Override
    public void addTalkBox(int talkIdx, Integer talkType, String talkId, String talkName, String talkHead, String talkSketch, Date talkDate, Boolean selected) {
        // 填充到对话框
        ListView<Pane> talkList = $("talkList", ListView.class);
        // 判断会话框是否有该对象
        ElementTalk elementTalk = CacheUtil.talkMap.get(talkId);
        if (null != elementTalk) {
            Node talkNode = talkList.lookup("#" + Ids.ElementTalkId.createTalkPaneId(talkId));
            if (null == talkNode) {
                talkList.getItems().add(talkIdx, elementTalk.pane());
                // 填充对话框消息栏
                fillInfoBox(elementTalk, talkName);
            }
            if (selected) {
                // 设置选中
                talkList.getSelectionModel().select(elementTalk.pane());
            }
            // 填充对话框消息栏
            fillInfoBox(elementTalk, talkName);
            return;
        }
        // 初始化对话框元素
        ElementTalk talkElement = new ElementTalk(talkId, talkType, talkName, talkHead, talkSketch, talkDate);
        CacheUtil.talkMap.put(talkId, talkElement);
        // 填充到对话框
        ObservableList<Pane> items = talkList.getItems();
        Pane talkElementPane = talkElement.pane();
        if (talkIdx >= 0) {
            items.add(talkIdx, talkElementPane);  // 添加到第一个位置
        } else {
            items.add(talkElementPane);           // 顺序添加
        }
        if (selected) {
            talkList.getSelectionModel().select(talkElementPane);
        }
        // 对话框元素点击事件
        talkElementPane.setOnMousePressed(event -> {
            // 填充消息栏
            fillInfoBox(talkElement, talkName);
            // 清除消息提醒
            Label msgRemind = talkElement.msgRemind();
            msgRemind.setUserData(new RemindCount(0));
            msgRemind.setVisible(false);
        });
        // 鼠标事件[移入/移出]
        talkElementPane.setOnMouseEntered(event -> {
            talkElement.delete().setVisible(true);
        });
        talkElementPane.setOnMouseExited(event -> {
            talkElement.delete().setVisible(false);
        });
        // 填充对话框消息栏
        fillInfoBox(talkElement, talkName);
        // 从对话框中删除
        talkElement.delete().setOnMouseClicked(event -> {
            talkList.getItems().remove(talkElementPane);
            $("info_pane_box", Pane.class).getChildren().clear();
            $("info_pane_box", Pane.class).setUserData(null);
            $("info_name", Label.class).setText("");
            talkElement.infoBoxList().getItems().clear();
            talkElement.clearMsgSketch();
            chatEvent.doEventDelTalkUser(super.userId, talkId);
        });
    }

    /**
     * 私有方法
     * 填充对话框消息内容
     *
     * @param talkElement 对话框元素
     * @param talkName    对话框名称
     */
    private void fillInfoBox(ElementTalk talkElement, String talkName) {
        String talkId = talkElement.pane().getUserData().toString();
        // 填充对话列表
        Pane info_pane_box = $("info_pane_box", Pane.class);
        String boxUserId = (String) info_pane_box.getUserData();
        // 判断是否已经填充[talkId]，当前已填充则返回
        if (talkId.equals(boxUserId)) return;
        ListView<Pane> listView = talkElement.infoBoxList();
        info_pane_box.setUserData(talkId);
        info_pane_box.getChildren().clear();
        info_pane_box.getChildren().add(listView);
        // 对话框名称
        Label info_name = $("info_name", Label.class);
        info_name.setText(talkName);
    }

    @Override
    public void addTalkMsgUserLeft(String talkId, String msg, Integer msgType, Date msgDate, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        ElementTalk talkElement = CacheUtil.talkMap.get(talkId);
        ListView<Pane> listView = talkElement.infoBoxList();
        TalkData talkUserData = (TalkData) listView.getUserData();
        Pane left = new ElementInfoBox().left(talkUserData.getTalkName(), talkUserData.getTalkHead(), msg, msgType);
        // 消息填充
        listView.getItems().add(left);
        // 滚动条
        listView.scrollTo(left);
        talkElement.fillMsgSketch(0 == msgType ? msg : "[表情]", msgDate);
        // 设置位置&选中
        chatView.updateTalkListIdxAndSelected(0, talkElement.pane(), talkElement.msgRemind(), idxFirst, selected, isRemind);
    }

    @Override
    public void addTalkMsgGroupLeft(String talkId, String userId, String userNickName, String userHead, String msg, Integer msgType, Date msgDate, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        // 自己的消息抛弃
        if (super.userId.equals(userId)) return;
        ElementTalk talkElement = CacheUtil.talkMap.get(talkId);
        if (null == talkElement) {
            GroupsData groupsData = (GroupsData) $(Ids.ElementTalkId.createFriendGroupId(talkId), Pane.class).getUserData();
            if (null == groupsData) return;
            addTalkBox(0, 1, talkId, groupsData.getGroupName(), groupsData.getGroupHead(), userNickName + "：" + msg, msgDate, false);
            talkElement = CacheUtil.talkMap.get(talkId);
            // 事件通知(开启与群组发送消息)
            chatEvent.doEventAddTalkGroup(super.userId, talkId);
        }
        ListView<Pane> listView = talkElement.infoBoxList();
        Pane left = new ElementInfoBox().left(userNickName, userHead, msg, msgType);
        // 消息填充
        listView.getItems().add(left);
        // 滚动条
        listView.scrollTo(left);
        talkElement.fillMsgSketch(0 == msgType ? userNickName + "：" + msg : userNickName + "：[表情]", msgDate);
        // 设置位置&选中
        chatView.updateTalkListIdxAndSelected(1, talkElement.pane(), talkElement.msgRemind(), idxFirst, selected, isRemind);
    }

    @Override
    public void addTalkMsgRight(String talkId, String msg, Integer msgType, Date msgData, Boolean idxFirst, Boolean selected, Boolean isRemind) {
        ElementTalk talkElement = CacheUtil.talkMap.get(talkId);
        ListView<Pane> listView = talkElement.infoBoxList();
        Pane right = new ElementInfoBox().right(userNickName, userHead, msg, msgType);
        // 消息填充
        listView.getItems().add(right);
        // 滚动条
        listView.scrollTo(right);
        talkElement.fillMsgSketch(0 == msgType ? msg : "[表情]", msgData);
        // 设置位置&选中
        chatView.updateTalkListIdxAndSelected(0, talkElement.pane(), talkElement.msgRemind(), idxFirst, selected, isRemind);
    }

    @Override
    public void addFriendGroup(String groupId, String groupName, String groupHead) {
        ElementFriendGroup elementFriendGroup = new ElementFriendGroup(groupId, groupName, groupHead);
        Pane pane = elementFriendGroup.pane();
        // 添加到群组列表
        ListView<Pane> groupListView = $("groupListView", ListView.class);
        ObservableList<Pane> items = groupListView.getItems();
        items.add(pane);
        groupListView.setPrefHeight(80 * items.size());
        $("friendGroupList", Pane.class).setPrefHeight(80 * items.size());

        // 群组，内容框[初始化，未装载]，承载群组信息内容，点击按钮时候填充
        Pane detailContent = new Pane();
        detailContent.setPrefSize(850, 560);
        detailContent.getStyleClass().add("friendGroupDetailContent");
        ObservableList<Node> children = detailContent.getChildren();

        Button sendMsgButton = new Button();
        sendMsgButton.setId(groupId);
        sendMsgButton.getStyleClass().add("friendGroupSendMsgButton");
        sendMsgButton.setPrefSize(176, 50);
        sendMsgButton.setLayoutX(337);
        sendMsgButton.setLayoutY(450);
        sendMsgButton.setText("发送消息");
        chatEventDefine.doEventOpenFriendGroupSendMsg(sendMsgButton, groupId, groupName, groupHead);
        children.add(sendMsgButton);

        // 添加监听事件
        pane.setOnMousePressed(event -> {
            clearViewListSelectedAll($("friendList", ListView.class), $("userListView", ListView.class));
            chatView.setContentPaneBox(groupId, groupName, detailContent);
        });
        chatView.setContentPaneBox(groupId, groupName, detailContent);
    }

    @Override
    public void addFriendUser(boolean selected, String userFriendId, String userFriendNickName, String userFriendHead) {
        ElementFriendUser friendUser = new ElementFriendUser(userFriendId, userFriendNickName, userFriendHead);
        Pane pane = friendUser.pane();
        // 添加到好友列表
        ListView<Pane> userListView = $("userListView", ListView.class);
        ObservableList<Pane> items = userListView.getItems();
        items.add(pane);
        userListView.setPrefHeight(80 * items.size());
        $("friendUserList", Pane.class).setPrefHeight(80 * items.size());
        // 选中
        if (selected) {
            userListView.getSelectionModel().select(pane);
        }

        // 好友，内容框[初始化，未装载]，承载好友信息内容，点击按钮时候填充
        Pane detailContent = new Pane();
        detailContent.setPrefSize(850, 560);
        detailContent.getStyleClass().add("friendUserDetailContent");
        ObservableList<Node> children = detailContent.getChildren();

        Button sendMsgButton = new Button();
        sendMsgButton.setId(userFriendId);
        sendMsgButton.getStyleClass().add("friendUserSendMsgButton");
        sendMsgButton.setPrefSize(176, 50);
        sendMsgButton.setLayoutX(337);
        sendMsgButton.setLayoutY(450);
        sendMsgButton.setText("发送消息");
        chatEventDefine.doEventOpenFriendUserSendMsg(sendMsgButton, userFriendId, userFriendNickName, userFriendHead);
        children.add(sendMsgButton);
        // 添加监听事件
        pane.setOnMousePressed(event -> {
            clearViewListSelectedAll($("friendList", ListView.class), $("groupListView", ListView.class));
            chatView.setContentPaneBox(userFriendId, userFriendNickName, detailContent);
        });
        chatView.setContentPaneBox(userFriendId, userFriendNickName, detailContent);
    }

    @Override
    public void addLuckFriend(String userId, String userNickName, String userHead, Integer status) {
        ElementFriendLuckUser friendLuckUser = new ElementFriendLuckUser(userId, userNickName, userHead, status);
        Pane pane = friendLuckUser.pane();
        // 添加到好友列表
        ListView<Pane> friendLuckListView = $("friendLuckListView", ListView.class);
        ObservableList<Pane> items = friendLuckListView.getItems();
        items.add(pane);
        // 点击事件
        friendLuckUser.statusLabel().setOnMousePressed(event -> {
            chatEvent.doEventAddLuckUser(super.userId, userId);
        });
    }

    @Override
    public double getToolFaceX() {
        return x() + width() - 960;
    }

    @Override
    public double getToolFaceY() {
        return y() + height() - 180;
    }

}
