package BASE_PACKAGE_NAME.rest_controllers;

import org.modelmapper.ModelMapper;


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
        new PersianCharNormalizer().normalize(xxx);
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
        Xxx xxx = xxxService.update(req);
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
                .body(result.getContent().stream().map((p)->mapper.map(p, SearchXxxRespRow.class)).collect(Collectors.toList()));
    }

}
