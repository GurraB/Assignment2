package se.mah.af6589.assignment2;

import android.graphics.Bitmap;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Gustaf Bohlin on 01/10/2017.
 */

public class Chat {

    private ArrayList<Message> messages = new ArrayList<>();
    private String groupName;

    public Chat(String groupName) {
        this.groupName = groupName;
        messages = new ArrayList<>();
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(String memberName, String text, String imageId, int port) {
        messages.add(new Message(memberName, text, imageId, port));
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public class Message {
        private String memberName;
        private String text;
        private String imageId;
        private int port;


        public Message(String memberName, String text) {
            this.memberName = memberName;
            this.text = text;
        }

        public Message(String memberName, String text, String imageId, int port) {
            this.memberName = memberName;
            this.text = text;
            this.imageId = imageId;
            this.port = port;
        }

        public String getMemberName() {
            return memberName;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
