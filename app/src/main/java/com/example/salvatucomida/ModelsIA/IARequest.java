package com.example.salvatucomida.ModelsIA;

import java.util.ArrayList;
import java.util.List;

public class IARequest {
    public String model = "openai/gpt-oss-20b";
    public List<Message> messages;

    public IARequest(String promptSistema, String promptUsuario) {
        messages = new ArrayList<>();
        messages.add(new Message("system", promptSistema));
        messages.add(new Message("user", promptUsuario));
    }

    public static class Message {
        public String role;
        public String content;
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}