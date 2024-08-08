package com.cgi.example.common.local;

import java.io.File;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * For a File URI to be clickable in the console logs three forward slashes are required e.g. file:///C:/Users/...
 * instead of file:/C:/Users/...
 */
public class ToClickableUriString implements Function<File, String> {

    @Override
    public String apply(File file) {
        String rawUri = file.toURI().toString();
        return rawUri.replace("file:/", "file:///");
    }
}
