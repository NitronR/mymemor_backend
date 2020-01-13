package com.mymemor.mymemor.forms;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Set;

public class AddMemoryForm {
    @NotBlank
    public String topic;
    public String content;
    public Date date_start;
    public Date date_end;
    public String location;
    public Set<String> photos;
}
