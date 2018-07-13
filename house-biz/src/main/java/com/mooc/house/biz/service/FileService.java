package com.mooc.house.biz.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FileService {

	//文件存储路径
	@Value("${file.path:}")
	private String filePath;


	/**
	 * 获取图片资源路径，相对路径
	 * @param files
	 * @return
	 */
	public List<String> getImgPaths(List<MultipartFile> files) {
	    if (Strings.isNullOrEmpty(filePath)) {
            filePath = getResourcePath();
        }
		List<String> paths = Lists.newArrayList();
		files.forEach(file -> {
			File localFile = null;
			if (!file.isEmpty()) {
				try {
					localFile =  saveToLocal(file, filePath);
					String path = StringUtils.substringAfterLast(StringUtils.replace(localFile.getAbsolutePath(), "\\", "/"), filePath);
					paths.add(path);
				} catch (IOException e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
		return paths;
	}

	/**
	 * 获取项目资源路径
	 * @return
	 */
	public static String getResourcePath(){
	  File file = new File(".");
	  String absolutePath = file.getAbsolutePath();
	  return absolutePath;
	}

	/**
	 * 保存到本地
	 * @param file
	 * @param filePath2
	 * @return
	 * @throws IOException
	 */
	private File saveToLocal(MultipartFile file, String filePath2) throws IOException {
		DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		LocalDateTime ldt = LocalDateTime.now();
		File newFile = new File(filePath + "/" + dtf2.format(ldt) +"/"+file.getOriginalFilename());
		//File newFile = new File(filePath + "/" + Instant.now().getEpochSecond() +"/"+file.getOriginalFilename());
	 if (!newFile.exists()) {
		 newFile.getParentFile().mkdirs();
		 newFile.createNewFile();
	 }
	 Files.write(file.getBytes(), newFile);
     return newFile;
	}

}
