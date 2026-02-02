package com.old.silence.dto;


import java.math.BigInteger;
import java.util.List;

/**
 * @author murrayZhang
 */
public class TreeDto {
    private BigInteger id;
    private String name;
    private BigInteger parentId;
    private List<TreeDto> children;

    public TreeDto(BigInteger id, String name, BigInteger parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public List<TreeDto> getChildren() {
        return children;
    }

    public void setChildren(List<TreeDto> children) {
        this.children = children;
    }
}
