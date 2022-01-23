package com.davidxie.Online.Facial.Recognition;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
public class ImageProcessorController {

    @RequestMapping(value = "/image_processor", method = RequestMethod.GET)
    public String getImageProcessor() {
        return "Please Use POST method on this endpoint to upload your image";
    }

    // Create a File Upload Controller
    @RequestMapping(value = "/image_processor", method = RequestMethod.POST)
    public String postImageProcessor(@RequestBody byte[] file, RedirectAttributes redirectAttributes) throws IOException {
        File convertFile = new File("/var/tmp/photo_"+UUID.randomUUID()+".png");
        FileOutputStream fout = new FileOutputStream(convertFile);
        fout.write(file);
        fout.close();
        return "File is upload successfully";
    }
}
