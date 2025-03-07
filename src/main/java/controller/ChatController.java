package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

public class ChatController extends HttpServlet {
    final static Logger logger = Logger.getLogger(ChatController.class.getName());

    @Override
    public void init() throws ServletException {
        super.init(); // 부모 클래스(HttpServlet)의 초기화 수행
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("I am doGet yeeeeee");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 환경 변수에서 API 키 로드
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("TOGETHER_API_KEY");

        // 인코딩 설정 (한글 사용 가능하도록)
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // 요청 본문(JSON)을 Message 객체로 변환
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(req.getInputStream(), Message.class);


        // API 요청 본문 생성
        String prompt = message.content();
        String body = """
                {
                    "model": "black-forest-labs/FLUX.1-schnell-Free",
                    "prompt": "%s",
                    "width": 1024,
                    "height": 768,
                    "steps": 4,
                    "n": 1,
                    "response_format": "b64_json"
                }
                """.formatted(prompt);

        // HTTP 클라이언트 생성 및 API 요청 전송
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.together.xyz/v1/images/generations"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .headers("Authorization", "Bearer %s".formatted(token), "Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // 응답에서 b64_json 데이터 추출
            String base64Image = mapper.readTree(responseBody)
                    .path("data").get(0).path("b64_json").asText();

            // 클라이언트에게 JSON 응답 반환
            String jsonResponse = "{ \"b64_json\": \"" + base64Image + "\" }";
            resp.getWriter().write(jsonResponse);
        } catch (Exception e) {
            logger.severe("API 요청 실패: " + e.getMessage());
            e.printStackTrace();  // 서버 콘솔에서 자세한 예외 로그 확인

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().write("{ \"error\": \"API 요청 실패\", \"message\": \"" + e.getMessage() + "\" }");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        logger.info("i am destroy");
    }
}

// JSON 요청을 받을 데이터 클래스
record Message(String content) {
}
