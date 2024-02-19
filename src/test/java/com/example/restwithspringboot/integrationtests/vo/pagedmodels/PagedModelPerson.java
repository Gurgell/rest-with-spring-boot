package com.example.restwithspringboot.integrationtests.vo.pagedmodels;

import com.example.restwithspringboot.integrationtests.vo.PersonVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class PagedModelPerson {
    @XmlElement(name = "content")
    private List<PersonVO> content;

    public PagedModelPerson() {}

    public PagedModelPerson(List<PersonVO> content) {
        this.content = content;
    }

    public List<PersonVO> getContent() {
        return content;
    }

    public void setContent(List<PersonVO> content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagedModelPerson that = (PagedModelPerson) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
