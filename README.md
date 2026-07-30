# Personal Assets

适用于 Halo 2.x 的资产管理插件。插件可在 Halo Console 中维护资产分组和资产条目，并为前台提供 `/personalassets` 页面路由，适合展示个人装备、工具、作品资源、常用服务或其他带图片的资产列表。

本插件由 [图库管理](https://www.halo.run/store/apps/app-BmQJW) 插件和原装备管理插件改编而来。

## 功能特性

- 在 Halo Console 中管理资产分组。
- 为每个分组添加资产条目。
- 支持资产名称、封面、资产地址、规格和描述。
- 支持从 Halo 附件库批量选择图片创建资产。
- 支持资产拖拽排序、批量删除和批量移动分组。
- 内置前台页面 `/personalassets`，安装启用后即可访问。
- 提供主题端 Finder，可在主题模板中自定义资产展示样式。

## 环境要求

- Halo 版本：`>= 2.22.0`
- Java 版本：`21`

## 安装与启用

1. 下载插件构建产物，例如 `plugin-personalassets-1.0.1.jar`。
2. 登录 Halo Console。
3. 进入「插件」页面，点击「安装插件」并上传 `.jar` 文件。
4. 安装完成后，在插件列表中启用「资产管理」插件。
5. 如果需要更新插件，可在插件详情页上传新版本，或参考 Halo 官方文档：<https://docs.halo.run/user-guide/plugins>

## 基本使用

### 1. 新建资产分组

1. 登录 Halo Console。
2. 进入左侧菜单中的「资产」页面。
3. 点击新建分组。
4. 填写分组名称和分组描述。
5. 保存后，左侧分组列表会显示新建的分组。

分组用于归类资产，例如「电脑设备」「软件工具」「摄影器材」「常用服务」等。

### 2. 添加资产

1. 在「资产」页面选择一个分组。
2. 点击「新增」。
3. 填写资产信息：
   - 名称：资产展示名称。
   - 封面：用于前台展示的图片。
   - 资产地址：点击详情按钮时跳转的链接。
   - 资产规格：例如型号、版本、配置等。
   - 描述：资产的补充说明。
4. 点击保存。

也可以通过「从附件库选择」批量创建资产。插件会使用所选图片作为资产封面，并根据附件名称生成资产名称。

### 3. 管理资产

在资产列表中可以进行以下操作：

- 点击资产卡片编辑资产信息。
- 拖拽资产卡片调整展示顺序。
- 勾选多个资产后批量删除。
- 勾选多个资产后批量移动到其他分组。
- 使用搜索框按资产名称、描述或地址搜索。

## 前台访问

插件内置前台页面，启用后可直接访问：

```text
/personalassets
```

分页地址：

```text
/personalassets/page/{page}
```

按分组筛选：

```text
/personalassets?group={groupName}
```

其中 `{groupName}` 是资产分组的 `metadata.name`，不是分组显示名称。

## 插件设置

进入 Halo Console 的插件设置页，可以调整以下配置：

| 配置项 | 说明 | 默认值 |
| --- | --- | --- |
| 页面标题 | 前台 `/personalassets` 页面的标题 | 资产 |
| 资产列表显示条数 | 前台分页时每页显示的资产数量 | 20 |

## 主题模板集成

插件已经提供内置模板。默认模板会尝试使用主题的公共布局 `modules/layout :: html(head,content)`，从而保留主题原有的页头、导航条和页脚。

如果当前主题没有提供这个公共布局，或者希望完全自定义页面结构，可以在主题中提供 `personalassets.html` 模板，Halo 会优先使用主题模板。

插件在主题端提供 `equipmentFinder`，可用于查询资产和分组。

### 查询全部资产

```html
<th:block th:each="equipment : ${equipmentFinder.listAll()}">
  <img th:src="${equipment.spec.cover}" th:alt="${equipment.spec.displayName}">
  <a th:href="${equipment.spec.url}" th:text="${equipment.spec.displayName}"></a>
</th:block>
```

### 按分组展示资产

```html
<th:block th:each="group : ${equipmentFinder.groupBy()}">
  <section>
    <h2 th:text="${group.spec.displayName}"></h2>
    <p th:if="${not #strings.isEmpty(group.spec.description)}" th:text="${group.spec.description}"></p>

    <div th:each="equipment : ${group.equipments}">
      <img th:src="${equipment.spec.cover}" th:alt="${equipment.spec.displayName}">
      <h3 th:text="${equipment.spec.displayName}"></h3>
      <p th:if="${not #strings.isEmpty(equipment.spec.specification)}" th:text="${equipment.spec.specification}"></p>
      <p th:if="${not #strings.isEmpty(equipment.spec.description)}" th:text="${equipment.spec.description}"></p>
      <a th:if="${not #strings.isEmpty(equipment.spec.url)}" th:href="${equipment.spec.url}" target="_blank">详情</a>
    </div>
  </section>
</th:block>
```

### 按指定分组查询

```html
<th:block th:each="equipment : ${equipmentFinder.listBy('equipment-group-example')}">
  <span th:text="${equipment.spec.displayName}"></span>
</th:block>
```

## 常见问题

### 前台访问 `/personalassets` 显示空白怎么办？

请先确认插件已经启用，并且已在 Console 的「资产」页面中新建分组和资产。资产必须归属于某个分组才会在分组展示中出现。

### 图片不显示怎么办？

请检查资产封面地址是否可以正常访问。如果使用 Halo 附件库图片，确认附件本身可公开访问。

### 分组筛选没有效果怎么办？

`group` 参数需要填写分组的 `metadata.name`。如果只知道分组显示名称，可以在浏览器开发者工具或 Halo 数据详情中查看对应的 `metadata.name`。

## 开发构建

本项目使用 Gradle 构建。

```bash
./gradlew build
```

Windows 环境可以使用：

```powershell
.\gradlew.bat build
```

构建完成后，插件包通常位于：

```text
build/libs/
```

## 相关链接

- 作者网站：<https://ffbf.top>
- 源码仓库：<https://github.com/dengchuanfu/personalassets>
- 问题反馈：<https://github.com/dengchuanfu/personalassets/issues>
