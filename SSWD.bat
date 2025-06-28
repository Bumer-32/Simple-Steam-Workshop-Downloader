@echo off

chcp 65001

for %%f in (SSWD-*.jar) do (
    java -jar "%%f"
    goto :end
)
:end
pause
