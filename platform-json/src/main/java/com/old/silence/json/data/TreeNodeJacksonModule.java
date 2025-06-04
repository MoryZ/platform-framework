package com.old.silence.json.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
        context.setMixInAnnotations(TreeNode.class, TreeNodeMixIn.class);
    }
    @JsonSerialize(using = TreeNodeSerializer.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface TreeNodeMixIn {
    }

}
