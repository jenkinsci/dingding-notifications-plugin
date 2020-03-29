package io.jenkins.plugins.model;

import io.jenkins.plugins.enums.BuildStatusEnum;
import io.jenkins.plugins.tools.AntdColor;
import io.jenkins.plugins.tools.Markdown;
import io.jenkins.plugins.tools.Utils;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
@Builder
public class BuildJobModel {

  private String projectName;

  private String projectUrl;

  private String jobName;

  private String jobUrl;

  private BuildStatusEnum statusType;

  private String duration;

  private String datetime;

  private String executorName;

  private String executorMobile;

  public String toMarkdown() {

    return new Markdown(
        String.format("# 项目： [%s](%s)", projectName, projectUrl),
        "---",
        String.format("- 任务：[%s](%s)", jobName, jobUrl),
        String.format("- 状态：%s",
            Utils.dye(
                statusType.getLabel(),
                statusType.getColor()
            )
        ),
        String.format("- 持续时间：%s", datetime),
        String.format("- 执行人：%s",
            StringUtils.isEmpty(executorMobile) ?
                executorName :
                Utils.dye(
                    ("@" + executorMobile),
                    AntdColor.BLUE.toString()
                )
        )
    ).value();
  }
}
