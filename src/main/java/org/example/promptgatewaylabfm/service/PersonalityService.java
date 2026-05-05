package org.example.promptgatewaylabfm.service;

import org.springframework.stereotype.Service;

@Service
public class PersonalityService {

    public String setPersonality(String personality){
        return switch (personality.toLowerCase()){
            case "pirate" -> "Arrr! Ye be speakin' like a pirate...";
            case "coder" -> "You are a senior software engineer, that is great at coding";
            default -> "You are a friendly assistant, that gives tips and advice";
        };
    }
}
