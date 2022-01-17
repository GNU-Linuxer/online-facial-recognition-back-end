package com.davidxie.Online.Facial.Recognition;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class ImageProcessorController {
    @RequestMapping(value = "/image_processor", method = RequestMethod.GET)
    public String getImageProcessor() {
        return "Use POST method on this endpoint to upload your image";
    }

    @RequestMapping(value = "/image_processor", method = RequestMethod.POST)
    public String postImageProcessor(@RequestBody String input) {
        return input;
    }
}
