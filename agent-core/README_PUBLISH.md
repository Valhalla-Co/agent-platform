# Publishing instructions for agent-core (GitHub Packages)

1) Add this to your ~/.m2/settings.xml:

```
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>${env.GITHUB_ACTOR}</username>
      <password>${env.GITHUB_TOKEN}</password>
    </server>
  </servers>
</settings>
```

2) Add distributionManagement to `agent-core/pom.xml` before publishing in CI (example):

```
<distributionManagement>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/OWNER/REPO</url>
  </repository>
</distributionManagement>
```

3) CI: use actions/setup-java + maven/ deploy with MAVEN settings and GITHUB_TOKEN as secret

4) Local publish for testing: `mvn -DskipTests clean deploy -DaltDeploymentRepository=github::default::https://maven.pkg.github.com/OWNER/REPO`
