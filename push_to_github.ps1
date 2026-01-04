# Flow Engine GitHub 上传脚本
# 使用说明：
# 1. 确保已安装 git
# 2. 在 GitHub 上创建一个空的仓库
# 3. 在终端运行此脚本，并传入您的仓库地址，例如：
#    .\push_to_github.ps1 -RepoUrl "https://github.com/您的用户名/flow-engine.git"


# .\push_to_github.ps1 -RepoUrl  "https://github.com/Awq678/flow-engine.git" 

param (
    [Parameter(Mandatory=$true)]
    [string]$RepoUrl
)

Write-Host "--- Start pushing project to GitHub ---" -ForegroundColor Cyan

# 1. 检查是否已经是 git 仓库
if (!(Test-Path .git)) {
    Write-Host "Initializing Git repository..."
    git init
}

# 2. 创建 .gitignore (如果不存在)
if (!(Test-Path .gitignore)) {
    Write-Host "Creating .gitignore..."
    @'
.idea/
target/
*.class
*.log
.settings/
.classpath
.project
.factorypath
bin/
'@ | Out-File -FilePath .gitignore -Encoding utf8
}

# 3. 添加文件
Write-Host "Adding files to stage..."
git add .

# 4. 提交
Write-Host "Committing changes..."
git commit -m "feat: complete flow-engine MVP stage 1"

# 5. 设置主分支
git branch -M main

# 6. 添加远程仓库
Write-Host "Configuring remote: $RepoUrl"
# 如果已存在 remote origin，先删除
git remote remove origin 2>$null
git remote add origin $RepoUrl

# 7. 推送
Write-Host "Pushing to GitHub..." -ForegroundColor Yellow
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host "--- Push Success! ---" -ForegroundColor Green
} else {
    Write-Host "--- Push Failed! Please check your repository URL or permissions. ---" -ForegroundColor Red
}

