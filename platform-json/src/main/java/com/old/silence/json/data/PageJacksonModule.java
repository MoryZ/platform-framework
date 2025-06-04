package com.old.silence.json.data;


import java.util.List;

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

public class PageJacksonModule extends Module{

    @Override
    public String getModuleName(){
        return "PageJacksonModule";
    }

    @Override
    public Version version() {
        return new Version(0, 1, 0 ,"", null, null);
    }

    @Override
    public void setupModule(Module.SetupContext context) {
        context.setMixInAnnotations(IPage.class, PageMixIn.class);
    }
    @JsonSerialize(using = PageSerializer.class)
    @JsonDeserialize(as = SimplePageImpl.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface PageMixIn {
    }

    static class SimplePageImpl<T> implements IPage<T> {

        private final IPage<T> delegate;

        SimplePageImpl(@JsonProperty("data") @JsonAlias("content") List<T> content, @JsonProperty("total") @JsonAlias({
                "totalElements", "total-elements", "total_elements", "totalelements", "TotalElements" }) long totalElements) {
            this.delegate = new PageImpl<>(content, totalElements);
        }

        @JsonIgnore
        @Override
        public List<OrderItem> orders() {
            return delegate.orders();
        }

        @JsonProperty("data")
        @Override
        public List<T> getRecords() {
            return delegate.getRecords();
        }

        @JsonIgnore
        @Override
        public IPage<T> setRecords(List<T> records) {
            delegate.setRecords(records);
            return this;
        }

        @JsonProperty("total")
        @Override
        public long getTotal() {
            return delegate.getTotal();
        }

        @JsonIgnore
        @Override
        public IPage<T> setTotal(long total) {
            delegate.setTotal(total);
            return this;
        }

        @JsonIgnore
        @Override
        public long getSize() {
            return delegate.getSize();
        }

        @JsonIgnore
        @Override
        public IPage<T> setSize(long size) {
            delegate.setSize(size);
            return this;
        }

        @JsonIgnore
        @Override
        public long getCurrent() {
            return delegate.getCurrent();
        }

        @JsonIgnore
        @Override
        public IPage<T> setCurrent(long current) {
            delegate.setCurrent(current);
            return this;
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return delegate.equals(obj);
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }
}
