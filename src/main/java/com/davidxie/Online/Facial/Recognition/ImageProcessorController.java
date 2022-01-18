package com.davidxie.Online.Facial.Recognition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ImageProcessorController {
    private final StorageService storageService;

    @Autowired
    public ImageProcessorController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "/image_processor", method = RequestMethod.GET)
    public String getImageProcessor() {
        return "Please Use POST method on this endpoint to upload your image";
    }

    // Create a File Upload Controller
    @RequestMapping(value = "/image_processor", method = RequestMethod.POST)
    public String postImageProcessor(@RequestBody MultipartFile file, RedirectAttributes redirectAttributes) {
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }
}
