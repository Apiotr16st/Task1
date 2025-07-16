package com.example.demo.dto;

public record CustomerUnreturnedDTO(String firstName, String lastName, String email, String filmTitle, Integer delay){
    @Override
    public String toString(){
        return "fistName:"+ firstName + ",lastName:"+lastName+",email:"+email+",tilte:"+filmTitle+",delay:"+delay+";";
    }
}
