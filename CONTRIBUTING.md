# Contributing to Dirigible

You can propose contributions by sending pull requests through GitHub.

And of course you can [report
issues](https://bugs.eclipse.org/bugs/enter_bug.cgi?product=Dirigible) and
[browse the current
ones](https://bugs.eclipse.org/bugs/buglist.cgi?list_id=12171979&product=Dirigible&query_format=advanced).

## Legal considerations

Please read the [Eclipse Foundation policy on accepting contributions via
Git](https://wiki.eclipse.org/Development_Resources/Contributing_via_Git).

Your contribution cannot be accepted unless you have an [Eclipse Foundation
Contributor License Agreement](https://www.eclipse.org/legal/CLA.php) in place.

Here is the checklist for contributions to be _acceptable_:

1. [create an account at Eclipse](https://dev.eclipse.org/site_login/), and
2. add your GitHub user name in your account settings, and
3. [log into the projects portal](https://projects.eclipse.org/) and look for
   ["Eclipse CLA"](https://projects.eclipse.org/user/sign/cla), and
4. ensure that you _sign-off_ your Git commits, and
5. ensure that you use the _same_ email address as your Eclipse Foundation in
   commits.

## Technical considerations

Please make sure your code compiles by running `mvn clean verify` which will
execute both unit and integration test phases.  Additionally, consider using 
http://travis-ci.org to validate your branches before you even put them into
pull requests.  All pull requests will be validated by Travis-ci in any case
and must pass before being merged.

Again, check that your author email in commits is the same as your GitHub account, and make sure that you sign-off every commit (`git commit
-s`).

Do not make pull requests from your `master` branch, please use topic branches
instead.

When submitting code, please make every effort to follow existing conventions
and style in order to keep the code as readable as possible.

Please provide meaningful commit messages.

Do not forget to mention the related Eclipse Bugzilla issue, if any.

Finally, a contribution is not a good contribution unless it comes with unit
tests, integration tests and documentation.
