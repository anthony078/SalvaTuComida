package com.example.salvatucomida.ModelsIA;

import java.util.List;

public class IAResponse {
    public List<Choice> choices;
    public static class Choice { public Message message;}
    public static class Message { public String content;}
}