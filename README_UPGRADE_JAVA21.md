# Upgrade notes: Target Java 21

This project has been prepared to target Java 21 (LTS). The following changes were applied to the `pom.xml`:

- `maven.compiler.source` and `maven.compiler.target` set to `21`
- `maven-compiler-plugin` `<release>` set to `21`
- `javafx.version` bumped to `21.0.2` (latest patch at time of upgrade; adjust if a newer OpenJFX patch is available)

What you should do locally

1) Install JDK 21

   - Download a JDK 21 distribution (Adoptium/Temurin, Oracle, or other vendor). Example: Eclipse Temurin 21.
   - Install it on Windows.
   - Set `JAVA_HOME` to the JDK 21 install folder and add `%JAVA_HOME%\bin` to `PATH`.

   In PowerShell (example, adjust the path to your JDK):

```powershell
# Example (adjust the path to where your JDK 21 is installed):
setx JAVA_HOME "C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.2.9-hotspot"
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.2.9-hotspot'
$env:Path = $env:JAVA_HOME + '\\bin;' + $env:Path
java -version
```

2) Build and test

```powershell
# maven must be available (or use the embedded mvn wrapper if present)
mvn -U clean package
# or run tests only
mvn test
```

3) JavaFX runtime notes

- For packaging or running on a platform, ensure you include the JavaFX native classifiers (e.g., `javafx-controls` with `classifier` for `win`), or use jlink/jpackage.
- If you encounter runtime issues with JavaFX, consider upgrading the `javafx.version` property to a newer OpenJFX patch release.

4) If build errors appear

- Some dependencies or code may need adjustments for Java 21 (module issues or removed/encapsulated APIs). If compilation fails, collect the compiler errors and update the project accordingly.

Next steps I can take for you (pick one):

- Install JDK 21 automatically (if you'd like and the environment tool is available).
- Run `mvn -U clean package` here and share/build output, then fix any compile/test failures.
- Update JavaFX dependency usage or add a module-info.java if you want full modularization.

If you want me to run the build and fix errors, tell me to run the build and I'll execute it and iterate on any failures.

---
Generated: automated edit to target Java 21.
