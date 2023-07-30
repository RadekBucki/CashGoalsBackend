# Contribution guidelines

## Branching

We use GitLab Flow branching model. That means that we have branches for each environment and develop branch for development.

```
master
  |
  |--- develop
  |       |
  |       |--- feature
  |       |--- hotfix
  |       |--- bugfix

```


### Branch naming

Branches are named as follows:

- `master` - production branch
- `develop` - development branch
- `feature/<issue-name>` - feature branch
- `hotfix/<issue-name>` - hotfix branch
- `bugfix/<issue-name>` - bugfix branch

Issue name should contain issue number from tracker and should be in kebab-case.

### Branching rules

- `master` branch is protected and can be merged only from `develop` and `hotfix` branch
- `develop` branch is protected and can be merged only from `feature` and `bugfix` branches

## Commit

- Commit should be small
- Commit message format: `<issue-number> <commit-message>`
  - For GitLab and GitHub issues: `#1 Commit message`
  - For Jira issues: `PROJ-1 Commit message`
- Commit messages should be in English
- Commit messages should be short and descriptive
- **If some changes are weird or hard to describe short extended commit message (after 2 enters) should be added**
- **If we don't finish issue by the end of the day, we should commit our changes and add `WIP` (work in progress) 
  to commit message and necessary push it to remote repository**
- Linguistic rules:
  - Commit messages should be in imperative mood
  - Commit messages should be in present tense
  - Commit messages should not end with a dot
  - Commit messages should start with capital letter
  - Commit messages should contain issue number

## Pull request and code review

- Pull request should be created from feature branch to develop branch
- Pull request should contain issue number in title
- Code review should be done by:
  - 1 person if project has less than 3 contributors
  - 2 persons if project has more than 3 contributors
- Junior developers could review and request changes others code, but they should not approve merge requests
- If we change someone's code, we should add that person as reviewer