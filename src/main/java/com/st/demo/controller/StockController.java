package com.st.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.demo.entity.SinfoEntity;
import com.st.demo.entity.SlistEntity;
import com.st.demo.service.RestTemplateToInterface;
import com.st.demo.service.SinfoService;
import com.st.demo.service.SlistService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 定期获取股票列表，并进行对比更新
 */
@RestController
@RequestMapping(value = "/analStock")
public class StockController {

}
