## 1. 定义数据模型

结合 EasyExcel 注解、JSR-303 校验注解：

```java
import com.alibaba.excel.annotation.ExcelProperty;
import javax.validation.constraints.*;
import lombok.Data;

@Data
public class UserExcelDto {
    @ExcelProperty("姓名")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @ExcelProperty("年龄")
    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄不能为负数")
    private Integer age;

    @ExcelProperty("邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;
}
```

---

## 2. 编写自定义监听器并结合 Validator 做校验

```java
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Validation;

public class UserExcelListener extends AnalysisEventListener<UserExcelDto> {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final List<UserExcelDto> validList = new ArrayList<>();
    private final List<String> errorMessages = new ArrayList<>();

    @Override
    public void invoke(UserExcelDto data, AnalysisContext context) {
        // JSR-303注解校验
        Set<ConstraintViolation<UserExcelDto>> violations = validator.validate(data);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<UserExcelDto> v : violations) {
                sb.append(v.getMessage()).append(";");
            }
            errorMessages.add("第" + (context.readRowHolder().getRowIndex() + 1) + "行：" + sb.toString());
        } else {
            validList.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 可在此做批量保存等后续处理
    }

    public List<UserExcelDto> getValidList() {
        return validList;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
```

---

## 3. 控制器层调用示例

```java
@PostMapping("/import")
public ResponseEntity<?> importExcel(MultipartFile file) throws IOException {
    UserExcelListener listener = new UserExcelListener();
    EasyExcel.read(file.getInputStream(), UserExcelDto.class, listener).sheet().doRead();

    if (!listener.getErrorMessages().isEmpty()) {
        return ResponseEntity.badRequest().body(listener.getErrorMessages());
    }

    // 批量保存
    userService.saveBatch(listener.getValidList());
    return ResponseEntity.ok("导入成功");
}
```

---

## 4. 业务唯一性等数据库校验（比如用户名、邮箱唯一）

在 `invoke` 方法中，可以注入 Service（如 `UserService`），对数据库进行唯一性校验：

```java
@Autowired
private UserService userService;

@Override
public void invoke(UserExcelDto data, AnalysisContext context) {
    // ... JSR-303校验
    if (userService.existsByEmail(data.getEmail())) {
        errorMessages.add("第" + (context.readRowHolder().getRowIndex() + 1) + "行：邮箱已存在;");
    }
    // ...
}
```

---

> 在实际项目中，我使用Spring Boot配合MyBatis-Plus和EasyExcel做过Excel数据的批量导入。对于数据校验，我主要采用了两种方式： 第一，利用Java的JSR-303注解（比如@NotNull、@Email等）结合Hibernate Validator，在EasyExcel的监听器内对每一行数据进行校验，能够自动捕获格式、必填、范围等基础错误； 第二，对于一些需要访问数据库的业务校验（比如唯一性检查），在监听器中注入Service，通过MyBatis-Plus查询数据库，判断数据是否重复或违规。 这样可以确保导入的数据既符合格式要求，也符合业务逻辑，保证了数据质量。 