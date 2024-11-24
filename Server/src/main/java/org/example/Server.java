package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Server {
    public static final int SERVER_PORT = 5009;
    public static final String ROOT_DIRECTORY = "html/";
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        //TODO: Проверить поентжжжжжа, код 127.0.0.1, порт 5009
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            //TODO: Проверитнтжжжжжжжжжжжа, код 127.0ддд.0.1, порт 5009
            System.out.println("Сервер запущен на порту " + SERVER_PORT);
            //TODO: Проверитьхххжжж поентжжжжжа, код 127.0ддд.0.1, порт 5009
            Map<String, IResourceHandler> resources = new HashMap<>();
            resources.put("/home", new HomeResourceHandler());

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Подключение от " + clientSocket.getInetAddress());

                try (InputStream inputStream = clientSocket.getInputStream();
                     OutputStream outputStream = clientSocket.getOutputStream();
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                     PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream))) {

                    // Чтение заголовков
                    String headerLine = bufferedReader.readLine();
                    String[] firstLine = headerLine.split("\\s+");
                    String method = firstLine[0];
                    String uri = firstLine[1];
                    String httpVers = firstLine[2];

                    //System.out.println(method + " " + uri + " " + httpVers);
                    //считывание запросов клиента
                    String contentLengthHeader = null;
                    while (headerLine != null && !headerLine.isEmpty()) {
                        System.out.println(headerLine);
                        String[] headerParts = headerLine.split(": ");
                        if (headerLine.startsWith("Content-Length: ")) {
                            contentLengthHeader = headerLine.split(": ")[1];
                        }
                        headerLine = bufferedReader.readLine();
                    }

                    if (uri.equals("/shutdown")) {
                        System.out.println("Команда остановки сервера получена.");
                        isRunning = false;
                        // Останавливаем сервер
                        String content = "Server is shutting down.";
                        printWriter.println("HTTP/1.1 200 OK");
                        printWriter.println("Content-Type: text/plain; charset=utf-8"); //простой текст
                        printWriter.println("Content-Length: " + content.length());
                        printWriter.println();
                        printWriter.flush();
                        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                        return;
                    }

                    // Извлечение параметров

                    Map<String, String> params = new HashMap<>();
                    if (method.equals("GET")) {
                        String[] parts = uri.split("\\?");
                        String resourceUri = parts[0];
                        String queryString = parts.length > 1 ? parts[1] : "";

                        if (!queryString.isEmpty()) {
                            String[] paramsPair = queryString.split("&");
                            for (String param : paramsPair) {
                                String[] keyValue = param.split("=");
                                params.put(keyValue[0], keyValue[1]);
                            }
                        }

                        // Обработка GET запроса
                        handleGET(resourceUri, printWriter, outputStream, params, resources);

                    } else if (method.equals("POST")) {
                        // Обработка POST запроса
                        if (contentLengthHeader != null) {
                            try {
                                int contentLength = Integer.parseInt(contentLengthHeader);//количество байт данных
                                byte[] postData = new byte[contentLength];
                                int bytesRead = 0;
                                while (bytesRead < contentLength) {
                                    bytesRead += inputStream.read(postData, bytesRead, contentLength - bytesRead);
                                }
                                String postDataString = new String(postData, StandardCharsets.UTF_8);
                                // System.out.println("POST data: " + postDataString);
                                String[] paramsPair = postDataString.split("&");
                                for (String param : paramsPair) {
                                    String[] keyValue = param.split("=");
                                    if (keyValue.length == 2) {
                                        params.put(keyValue[0], keyValue[1]);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        // Неизвестный метод
                        printWriter.println("HTTP/1.1 405 Method Not Allowed");
                        printWriter.println("Content-Type: text/plain");
                        printWriter.println("Content-Length: 0");
                        printWriter.println();
                        printWriter.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleGET(String uri, PrintWriter printWriter, OutputStream outputStream, Map<String, String> params, Map<String, IResourceHandler> resources) throws IOException {
        if (uri.equals("/")) {
            uri = "/index.html";
        }

        Path filePath = Paths.get(System.getProperty("user.dir"), ROOT_DIRECTORY, uri.substring(1));

        IResourceHandler resourceHandler = resources.get(uri);
        if (resourceHandler != null) {
            ResponceContent responseContent = resourceHandler.handle(params);
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type: " + responseContent.getMimeType());
            printWriter.println("Content-Length: " + responseContent.getContent().length);
            printWriter.println();
            printWriter.flush();
            outputStream.write(responseContent.getContent());
            outputStream.flush();
        } else if (Files.exists(filePath)) {
            String mimeType = getContentType(filePath.toString());
            byte[] content = Files.readAllBytes(filePath);

            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type: " + mimeType);
            printWriter.println("Content-Length: " + content.length);
            printWriter.println();
            printWriter.flush();
            outputStream.write(content);
            outputStream.flush();
        } else {
            String notFoundResponse = "<html><body><h1>404 Not Found</h1></body></html>";
            printWriter.println("HTTP/1.1 404 Not Found");
            printWriter.println("Content-Type: text/html; charset=utf-8");
            printWriter.println("Content-Length: " + notFoundResponse.length());
            printWriter.println();
            printWriter.flush();

            outputStream.write(notFoundResponse.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }

    public static String getContentType(String filePath) {
        if (filePath.endsWith(".html")) {
            return "text/html";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filePath.endsWith(".png")) {
            return "image/png";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else {
            return "application/octet-stream";
        }
    }

}
