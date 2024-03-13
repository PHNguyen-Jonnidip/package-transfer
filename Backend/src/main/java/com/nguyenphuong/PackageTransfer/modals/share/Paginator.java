package com.nguyenphuong.PackageTransfer.modals.share;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class Paginator<T> {
  private Integer page;
  private Integer size;
  private Long total;
  private List<T> results;

  public Paginator(Integer size, Long total, Integer page, List<T> results) {
    this.size = size;
    this.total = total;
    this.page = page;
    this.results = results;
  }

  public Paginator(Page<T> page) {
    this.size = page.getSize();
    this.total = page.getTotalElements();
    this.page = page.getNumber();
    this.results = page.getContent();
  }
}
