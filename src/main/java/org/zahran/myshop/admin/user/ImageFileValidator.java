package org.zahran.myshop.admin.user;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {


    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        boolean result = true;

        if (file != null && !file.isEmpty()){
            String contentType = file.getContentType();

            if (!isSupportedContentType(contentType)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "Only PNG or JPG images are allowed.")
                        .addConstraintViolation();

                result = false;
            }
        }
        return result;
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}
