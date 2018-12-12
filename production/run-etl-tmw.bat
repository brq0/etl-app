::RUNNING ETL-TMW

::TYPE LOCATION OF ETL_UI PROJECT
::set etl_ui_loc=YOUR_ETL_UI_LOCATON

::TYPE PATH OF ETL-APP-TMW-1.0.0
::set etl_app_jar_path=YOUR ETL-APP-TMW-1.0.0_PATH

::OR USE DEFAULTS AND MOVE DOWNLOADED ETL-UI TO C:\etl-tmw\
::AND MOVE etl-app-tmw-1.0.0.jar FROM ETL-APP\production

::DEFAULTS
set etl_ui_loc=C:\etl-tmw\etl-ui
set etl_app_jar_path=C:\etl-tmw\etl-app-tmw-1.0.0.jar

::npm install --cwd %etl_ui_loc% --prefix %etl_ui_loc%\

start cmd /k npm start --prefix %etl_ui_loc%
start cmd /k java -jar %etl_app_jar_path%
