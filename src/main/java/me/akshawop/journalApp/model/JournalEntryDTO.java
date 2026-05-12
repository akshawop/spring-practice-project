package me.akshawop.journalApp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JournalEntryDTO {
        @NotBlank(message = "Title cannot be empty", groups = { OnCreate.class })
        @Size(max = 50, message = "Title should not exceed 50 characters")
        String title;

        @NotBlank(message = "Content cannot be empty")
        @Size(max = 10000, message = "Content cannot exceed 10000 characters")
        String content;

        public static interface OnCreate {
        }
}
