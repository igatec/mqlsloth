![Java CI](https://github.com/igatec/mqlsloth/workflows/Java%20CI/badge.svg?branch=master) 
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/igatec/mqlsloth/blob/master/LICENSE.txt)     
  
#   MQLSloth
**WIP**  
Now library is under migration. Not all functionality is working! Please stay tuned!  
##  Install
*   package library
```cmd
mvn clean deploy -Ddeploy=build -Dscope=em 
```
*   add lib to MQL classpath    
*   create JPO Sloth   
```cmd
add prog Sloth java;
```

##  Usage
```cmd
#   help
exec prog Sloth -h;

#   export
exec prog Sloth -e -l /Customizations/db -p type *Doc*;

#   import 
exec prog Sloth -i -l /Customizations/db -p type *Doc*;

#   create empty object file definition
exec prog Sloth -n type MyPart -l /Customizations/;
``` 
