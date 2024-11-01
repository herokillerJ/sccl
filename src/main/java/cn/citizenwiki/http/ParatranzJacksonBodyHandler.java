package cn.citizenwiki.http;

import cn.citizenwiki.model.dto.paratranz.PZFile;
import cn.citizenwiki.model.dto.paratranz.PZTranslation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

public class ParatranzJacksonBodyHandler {

    private static final ObjectMapper om;
    public static final JacksonBodyHandler<List<PZFile>> LIST_FILE = new JacksonBodyHandler<>(new TypeReference<>() {}, om);
    public static final JacksonBodyHandler<List<PZTranslation>> LIST_TRANSLATION = new JacksonBodyHandler<>(new TypeReference<>() {}, om);

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        om = objectMapper;
    }

}
