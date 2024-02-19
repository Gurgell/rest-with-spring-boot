package configs;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

public class TestConfigs {

    public static final int SERVER_PORT = 8888;
    public static final String HEADER_PARAM_AUTHORIZATION = "Authorization";
    public static final String HEADER_PARAM_ORIGIN = "Origin";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_XML_VIA_HEADER_PARAM = "xml";
    public static final String ORIGIN_GURGEL = "https://gurgel.com.br";
    public static final String ORIGIN_SEMERU = "https://semeru.com.br";
}
