## Summary

<!-- Same format as the summary in AGENTS.md. Write "none" where nothing applies. -->

Done: 
Files: 
Docs changed: 
Assumptions: 
Differs from docs / not done: 
Verified: 
Untested / manual steps for a human: 

## Checklist

- [ ] One task from `docs/STATUS.md`, on a `feature/` or `fix/` branch
- [ ] `bash scripts/check-layers.sh` passes
- [ ] `./gradlew testDebugUnitTest lintDebug assembleDebug` passes (Windows: `gradlew.bat ...`)
- [ ] New pure logic has unit tests; wireless code is marked untested with manual steps above
- [ ] Docs this change affects are updated in this PR, and `docs/STATUS.md` is updated
- [ ] No new dependency, permission, package, protocol field or design token without approval
- [ ] No secrets, keys, join codes or HMACs logged; no keystores, `local.properties` or real photos committed
- [ ] Strings in `strings.xml`, colors and sizes from `ui/theme/` only
- [ ] Commit messages follow `<type>(<scope>): <short description>`
