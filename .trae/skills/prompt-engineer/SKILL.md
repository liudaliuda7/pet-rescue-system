---
name: "prompt-engineer"
description: "Generates multi-round iterative prompts for full-stack projects based on feedback. Invoke when user needs to design user prompts for model evaluation, feature iteration, or bug fix rounds."
---

# Prompt Engineer

This skill designs multi-round user prompts for full-stack project iteration. It ensures prompts meet constraints: single field ≤ 300 chars, no duplicates, avoid overly simple tasks, and simulate real user scenarios.

## Constraints

1. **Length**: Each prompt field must not exceed 300 characters.
2. **Uniqueness**: No duplicate prompts across rounds or branches.
3. **Complexity**: Avoid overly simple tasks (single-file change, no repo context, 1-round solvable, single language).
4. **Task Types**: 0-1 code generation, Feature iteration, Bug fix, Code understanding, Refactoring, Engineering, Testing.
5. **Difficulty**: Simple, General, Hard, Hell.
6. **Output Format**: User prompt + task type + difficulty + scope.

## Workflow

### Round 1 (Initial Prompt)

- Analyze the project structure and existing code.
- Design a prompt that:
  - Builds on existing features (Feature iteration) or fixes known issues (Bug fix).
  - Spans multiple modules/files (frontend + backend).
  - Requires understanding of repo context.
  - Has moderate complexity (General or Hard).
- Output format:

```
## Round 1 User Prompt

> [Prompt text, ≤300 chars]

**任务类型**: [Type]
**任务难度**: [Difficulty]
**涉及范围**: [Backend files, Frontend files, DB changes]
```

### Round N (Feedback-Based Prompt)

When user provides feedback, analyze it and generate the next prompt:

- **Identify the issue**: What went wrong? (bug, missing feature, performance, etc.)
- **Simulate real user behavior**: The next prompt should reflect how a real user would report this issue.
  - Example: If previous output had a bug, the next prompt might just be an error message + "帮我修一下".
  - Example: If previous output was incomplete, the next prompt might point out the missing part directly.
- **Provide scoring and explanation** (if requested):

```
#feedback [score] [summary]
[Detailed explanation of issues found]

## Round N User Prompt

> [Prompt text, ≤300 chars]

**任务类型**: [Type]
**任务难度**: [Difficulty]
**涉及范围**: [Files/modules]
```

### New Branch

When user asks to start a new branch:
- Review the current codebase state.
- Design a completely different feature or task.
- Ensure no overlap with previous branch prompts.

## Example Output

### Round 1

```
## Round 1 User Prompt

> 基于Spring Boot + Vue 2的流浪动物救助系统，目前动物详情页仅展示基本信息。需要在动物详情页下方新增「健康档案」时间轴，展示该动物的历史健康记录（就诊日期、医生、内容）。同时后台管理端的「流浪动物管理」列表增加一列「健康记录数」并支持点击跳转查看该动物的健康档案。后端需要补充按animalId查询健康记录的公开接口，并确保数据权限（station角色只能看到本站动物的健康记录）。

**任务类型**: Feature 迭代
**任务难度**: 一般
**涉及范围**: 后端（HealthRecordController、AnimalController）、前端（AnimalDetail.vue、AnimalMgmt.vue）、数据库（已有health_record表）
```

### Round 2 (Based on Feedback)

```
#feedback 1 后端JAR包为旧版本，缺少 /health/public 白名单配置和 publicList 接口，导致请求被拦截器拦截返回401，前端表现为404错误，健康档案时间轴无法加载数据。

## Round 2 User Prompt

> 动物详情页下面的健康档案时间轴加载不出来，浏览器控制台报错401，然后变成404。我查了一下，后端JAR包是12天前编译的旧版本，里面没有 `/health/public` 的白名单配置，也没有 `publicList` 接口。你帮我把后端的拦截器白名单和公开接口补上，然后重新编译打包，确认前端能正常拉到数据。

**任务类型**: Bug 修复
**任务难度**: 一般
**涉及范围**: 后端（WebConfig.java 白名单、HealthRecordController.java 新增公开接口）、编译验证
```

## Rules

- NEVER exceed 300 characters per prompt field.
- NEVER generate duplicate prompts.
- NEVER create overly simple tasks (must span multiple files or require repo context).
- ALWAYS simulate real user behavior in follow-up prompts.
- ALWAYS include task type, difficulty, and scope in output.
- ALWAYS review codebase before generating prompts.
