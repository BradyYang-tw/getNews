package com.wistron.javacodebase.templateFeature;

import com.wistron.javacodebase.common.dto.ApiResponse;
import com.wistron.javacodebase.common.dto.PageResponse;
import com.wistron.javacodebase.templateFeature.vo.TemplateFeatureListResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/template-feature")
@Tag(name = "Template Feature API", description = "新增模組時可參考的標準 Controller 寫法")
public class TemplateFeatureController {

  private final TemplateFeatureService templateFeatureService;

  public TemplateFeatureController(TemplateFeatureService templateFeatureService) {
    this.templateFeatureService = templateFeatureService;
  }

  @Operation(summary = "取得 Template Feature 清單")
  @GetMapping("/list")
  public ResponseEntity<ApiResponse<PageResponse<TemplateFeatureListResponseVO>>> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    PageResponse<TemplateFeatureListResponseVO> result = templateFeatureService.list(page, size);
    return ResponseEntity.ok(new ApiResponse<>(true, "SUCCESS", result));
  }
}
