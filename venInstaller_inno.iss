; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "HCSWS Multiple Database Querier"
#define MyAppVersion "1.3.2"
#define MyAppPublisher "Venolin Naidoo"
#define MyAppURL "NA"
#define MyAppExeName "MDBQ.jar"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{05BB06C9-5D15-4193-8117-C55C4BC94C8C}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DisableProgramGroupPage=yes
OutputBaseFilename=setup
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Dirs]
Name: "{app}"; Permissions: users-full

[Files]
Source: "C:\Users\Venolin\Documents\NetBeansProjects\HCSWS-Multiple-Database-Querier\dist\HCSWS_Multiple_Database_Querier.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Venolin\Documents\NetBeansProjects\HCSWS-Multiple-Database-Querier\config.properties"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Venolin\Documents\NetBeansProjects\HCSWS-Multiple-Database-Querier\dist\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{commonprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: shellexec postinstall skipifsilent

