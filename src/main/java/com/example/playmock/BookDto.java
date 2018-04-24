package com.example.playmock;

import lombok.Value;
import org.springframework.data.repository.query.Param;

@Value
public class BookDto {
    String title, authorName;
}
