package com.example.rabbitmqpublisherdemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {

    private ResponseStatusData status;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseStatusData {
        private String message;
        private String description;
    }

    public static CommonResponse genResponse(String message, String description) {
        return new CommonResponse(new ResponseStatusData(message, description));
    }

    public static CommonResponse genSuccessResponse() {
        return genResponse("Success", "Success");
    }
}
