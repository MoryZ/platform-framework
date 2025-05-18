package com.old.silence.json.data;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.old.silence.page.PageImpl;

import java.util.List;

public class TreeNodeJacksonModule extends Module{

    @Override
    public String getModuleName(){
        return "TreeNodeJacksonModule";
    }

    @Override
    public Version version() {
        return new Version(0, 1, 0 ,"", null, null);
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(IPage.class, PageMixIn.class);
    }
    @JsonSerialize(using = TreeNodeSerializer.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface PageMixIn {
    }

}
