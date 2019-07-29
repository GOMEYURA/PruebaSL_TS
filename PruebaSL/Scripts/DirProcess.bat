FOR /f %%a in ('WMIC OS GET LocalDateTime ^| find "."') DO Set _DTS=%%a
Set _datetime=%_DTS:~0,4%-%_DTS:~4,2%-%_DTS:~6,2%@%_DTS:~8,2%-%_DTS:~10,2%-%_DTS:~12,2%
REN ".\test-output" "test-output-%_datetime%"
MOVE ".\test-output-%_datetime%" ".\Reports"
MKDIR ".\test-output\Screenshots"
EXIT