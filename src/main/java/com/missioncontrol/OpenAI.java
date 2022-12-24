package com.missioncontrol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.theokanning.openai.*;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;

public class OpenAI {
    public static String fluentSentence(String data) throws FileNotFoundException, IOException {
        Props keys = Props.getProps();
        OpenAiService service = new OpenAiService(keys.openAIKey);

        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Write a fluent sentence with the follow data and then give it a danger level out of 1 to 5: " + data + ".")
                .model("text-davinci-003")
                .maxTokens(4000)
                .echo(false)
                .build();

        List<CompletionChoice> choices = service.createCompletion(completionRequest).getChoices();
        String res = choices.get(0).getText();
        return res;
    }
}
