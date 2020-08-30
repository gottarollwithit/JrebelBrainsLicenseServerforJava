package com.vvvtimes.server;

import com.vvvtimes.JrebelUtil.JrebelSign;
import com.vvvtimes.util.rsasign;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainServer extends AbstractHandler {

    public static Map<String, String> parseArguments(String[] args) {
        Map<String, String> params = new HashMap<>();

        String option = null;
        for (final String arg : args) {
            if (arg.charAt(0) == '-') {
                if (arg.length() < 2) {
                    throw new IllegalArgumentException("Error at argument " + arg);
                }
                option = arg.substring(1);
            } else {
                params.put(option, arg);
            }
        }
        return params;
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> arguments = parseArguments(args);
        String port = arguments.get("p");

        if (port == null || !port.matches("\\d+")) {
            port = "8081";
        }

        Server server = new Server(Integer.parseInt(port));
        server.setHandler(new MainServer());
        server.start();

        System.out.println("License Server started at http://localhost:" + port);
        System.out.println("JetBrains Activation address was: http://localhost:" + port + "/");
        System.out.println("JRebel 7.1 and earlier version Activation address was: http://localhost:" + port + "/{tokenname}, with any email.");
        System.out.println("JRebel 2018.1 and later version Activation address was: http://localhost:" + port + "/{guid}(eg:http://localhost:" + port + "/" + UUID.randomUUID().toString() + "), with any email.");

        server.join();
    }

    @Override
    public void handle(String target, Request baseRequest, jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) throws IOException {

        System.out.println(target);
        switch (target) {
            case "/" -> indexHandler(baseRequest, request, response);
            case "/jrebel/leases", "/agent/leases" -> jrebelLeasesHandler(baseRequest, request, response);
            case "/jrebel/leases/1", "/agent/leases/1" -> jrebelLeases1Handler(baseRequest, request, response);
            case "/jrebel/validate-connection" -> jrebelValidateHandler(baseRequest, response);
            case "/rpc/ping.action" -> pingHandler(baseRequest, request, response);
            case "/rpc/obtainTicket.action" -> obtainTicketHandler(baseRequest, request, response);
            case "/rpc/releaseTicket.action" -> releaseTicketHandler(baseRequest, request, response);
            default -> response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void jrebelValidateHandler(Request baseRequest, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String jsonStr = "{\n" +
                "    \"serverVersion\": \"3.2.4\",\n" +
                "    \"serverProtocolVersion\": \"1.1\",\n" +
                "    \"serverGuid\": \"a1b4aea8-b031-4302-b602-670a990272cb\",\n" +
                "    \"groupType\": \"managed\",\n" +
                "    \"statusCode\": \"SUCCESS\",\n" +
                "    \"company\": \"Administrator\",\n" +
                "    \"canGetLease\": true,\n" +
                "    \"licenseType\": 1,\n" +
                "    \"evaluationLicense\": false,\n" +
                "    \"seatPoolType\": \"standalone\"\n" +
                "}\n";
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        String body = jsonObject.toString();
        response.getWriter().print(body);
    }

    private void jrebelLeases1Handler(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String username = request.getParameter("username");
        baseRequest.setHandled(true);
        String jsonStr = "{\n" +
                "    \"serverVersion\": \"3.2.4\",\n" +
                "    \"serverProtocolVersion\": \"1.1\",\n" +
                "    \"serverGuid\": \"a1b4aea8-b031-4302-b602-670a990272cb\",\n" +
                "    \"groupType\": \"managed\",\n" +
                "    \"statusCode\": \"SUCCESS\",\n" +
                "    \"msg\": null,\n" +
                "    \"statusMessage\": null\n" +
                "}\n";
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        if (username != null) {
            jsonObject.put("company", username);
        }
        String body = jsonObject.toString();
        response.getWriter().print(body);

    }

    private void jrebelLeasesHandler(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String clientRandomness = request.getParameter("randomness");
        String username = request.getParameter("username");
        String guid = request.getParameter("guid");
        boolean offline = Boolean.parseBoolean(request.getParameter("offline"));
        String validFrom = "null";
        String validUntil = "null";
        if (offline) {
            String clientTime = request.getParameter("clientTime");
            long clinetTimeUntil = Long.parseLong(clientTime) + 180L * 24 * 60 * 60 * 1000;
            validFrom = clientTime;
            validUntil = String.valueOf(clinetTimeUntil);
        }
        baseRequest.setHandled(true);
        String jsonStr = "{\n" +
                "    \"serverVersion\": \"3.2.4\",\n" +
                "    \"serverProtocolVersion\": \"1.1\",\n" +
                "    \"serverGuid\": \"a1b4aea8-b031-4302-b602-670a990272cb\",\n" +
                "    \"groupType\": \"managed\",\n" +
                "    \"id\": 1,\n" +
                "    \"licenseType\": 1,\n" +
                "    \"evaluationLicense\": false,\n" +
                "    \"signature\": \"OJE9wGg2xncSb+VgnYT+9HGCFaLOk28tneMFhCbpVMKoC/Iq4LuaDKPirBjG4o394/UjCDGgTBpIrzcXNPdVxVr8PnQzpy7ZSToGO8wv/KIWZT9/ba7bDbA8/RZ4B37YkCeXhjaixpmoyz/CIZMnei4q7oWR7DYUOlOcEWDQhiY=\",\n" +
                "    \"serverRandomness\": \"H2ulzLlh7E0=\",\n" +
                "    \"seatPoolType\": \"standalone\",\n" +
                "    \"statusCode\": \"SUCCESS\",\n" +
                "    \"offline\": " + offline + ",\n" +
                "    \"validFrom\": " + validFrom + ",\n" +
                "    \"validUntil\": " + validUntil + ",\n" +
                "    \"company\": \"Administrator\",\n" +
                "    \"orderId\": \"\",\n" +
                "    \"zeroIds\": [\n" +
                "        \n" +
                "    ],\n" +
                "    \"licenseValidFrom\": 1490544001000,\n" +
                "    \"licenseValidUntil\": 1691839999000\n" +
                "}";

        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        if (clientRandomness == null || username == null || guid == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            JrebelSign jrebelSign = new JrebelSign();
            jrebelSign.toLeaseCreateJson(clientRandomness, guid, offline, validFrom, validUntil);
            String signature = jrebelSign.getSignature();
            jsonObject.put("signature", signature);
            jsonObject.put("company", username);
            String body = jsonObject.toString();
            response.getWriter().print(body);
        }
    }

    private void releaseTicketHandler(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String salt = request.getParameter("salt");
        baseRequest.setHandled(true);
        if (salt == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            String xmlContent = "<ReleaseTicketResponse><message></message><responseCode>OK</responseCode><salt>" + salt + "</salt></ReleaseTicketResponse>";
            String xmlSignature = rsasign.Sign(xmlContent);
            String body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
            response.getWriter().print(body);
        }
    }

    private void obtainTicketHandler(Request baseRequest, HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String salt = request.getParameter("salt");
        String username = request.getParameter("userName");
        String prolongationPeriod = "607875500";
        baseRequest.setHandled(true);
        if (salt == null || username == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            String xmlContent = "<ObtainTicketResponse><message></message><prolongationPeriod>" + prolongationPeriod + "</prolongationPeriod><responseCode>OK</responseCode><salt>" + salt + "</salt><ticketId>1</ticketId><ticketProperties>licensee=" + username + "\tlicenseType=0\t</ticketProperties></ObtainTicketResponse>";
            String xmlSignature = rsasign.Sign(xmlContent);
            String body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
            response.getWriter().print(body);
        }
    }

    private void pingHandler(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String salt = request.getParameter("salt");
        baseRequest.setHandled(true);
        if (salt == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            String xmlContent = "<PingResponse><message></message><responseCode>OK</responseCode><salt>" + salt + "</salt></PingResponse>";
            String xmlSignature = rsasign.Sign(xmlContent);
            String body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
            response.getWriter().print(body);
        }

    }

    private void indexHandler(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        int port = request.getServerPort();
        String html = "<h1>Hello,This is a Jrebel & JetBrains License Server!</h1>";
        html += "<p>License Server started at http://localhost:" + port;
        html += "<p>JetBrains Activation address was: <span style='color:red'>http://localhost:" + port + "/";
        html += "<p>JRebel 7.1 and earlier version Activation address was: <span style='color:red'>http://localhost:" + port + "/{tokenname}</span>, with any email.";
        html += "<p>JRebel 2018.1 and later version Activation address was: http://localhost:" + port + "/{guid}(eg:<span style='color:red'>http://localhost:" + port + "/" + UUID.randomUUID().toString() + "</span>), with any email.";

        response.getWriter().println(html);

    }
}