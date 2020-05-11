package com.mymemor.mymemor.forms;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class AddMemoryForm {
    @NotBlank
    public String topic;
    @NotBlank
    public String content;
    public Date start_date;
    public Date end_date;
    public String location;
    public Set<String> photos;
    public List<Long> peopleIds;
}
