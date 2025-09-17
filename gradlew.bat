@ECHO OFF
SETLOCAL
WHERE gradle >NUL 2>&1
IF %ERRORLEVEL% NEQ 0 (
  ECHO Gradle is required to build this project.
  EXIT /B 1
)
SET CMD_ARGS=
:args
IF "%1"=="" GOTO end
  SET CMD_ARGS=%CMD_ARGS% %1
  SHIFT
  GOTO args
:end
gradle %CMD_ARGS%
