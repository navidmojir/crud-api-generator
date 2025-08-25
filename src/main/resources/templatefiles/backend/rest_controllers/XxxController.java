package com.example.demo.rest_controllers;

import com.example.demo.dtos.xxx.*;
import com.example.demo.entities.Xxx;
import com.example.demo.services.XxxService;
import ir.mojir.spring_boot_commons.dtos.SearchDto;
import ir.mojir.spring_boot_commons.helpers.PersianCharNormalizer;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/xxxs")
public class XxxController {

    @Autowired
    private XxxService xxxService;

    @Autowired
    private ModelMapper mapper;

    @PostMapping
    public CreateXxxResp create(@Valid @RequestBody CreateXxxReq req)
    {
        new PersianCharNormalizer().normalize(req);
        Xxx xxx = xxxService.create(req);
        return mapper.map(xxx, CreateXxxResp.class);
    }

    @GetMapping("/{id}")
    public GetXxxResp get(@PathVariable long id)
    {
        Xxx entity = xxxService.get(id);
        return mapper.map(entity, GetXxxResp.class);
    }

    @PutMapping("/{id}")
    public UpdateXxxResp update(@PathVariable long id, @Valid @RequestBody UpdateXxxReq req)
    {
        new PersianCharNormalizer().normalize(req);
        Xxx xxx = xxxService.update(id, req);
        return mapper.map(xxx, UpdateXxxResp.class);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        xxxService.delete(id);
    }


    @PostMapping("/search")
    public ResponseEntity<List<SearchXxxRespRow>> search(@Valid @RequestBody SearchDto<XxxSearchFilter> req)
    {
        new PersianCharNormalizer().normalize(req.getFilters());
        Page<Xxx> result = xxxService.search(req);
        return ResponseEntity.ok()
                .header("X-TOTAL-COUNT", String.valueOf(result.getTotalElements()))
                .body(result.getContent().stream().map((p)->mapper.map(p, SearchXxxRespRow.class))
                        .collect(Collectors.toList()));
    }

}
