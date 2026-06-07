#!/usr/bin/env bash
# push.sh — stage, commit, and push as manniru@gmail.com (no AI co-author).
#
# Usage:
#   ./push.sh "Your commit message"
#   ./push.sh            # uses a default message
#
# Commits to the current branch and pushes to origin.
set -euo pipefail

NAME="MUHAMMAD MANNIR AHMAD"
EMAIL="manniru@gmail.com"
MSG="${*:-Update}"

# Force both author and committer identity (overrides any local git config).
export GIT_AUTHOR_NAME="$NAME"
export GIT_AUTHOR_EMAIL="$EMAIL"
export GIT_COMMITTER_NAME="$NAME"
export GIT_COMMITTER_EMAIL="$EMAIL"

git add -A

if git diff --cached --quiet; then
  echo "Nothing to commit."
else
  git commit -m "$MSG"
fi

BRANCH="$(git rev-parse --abbrev-ref HEAD)"
echo "Pushing to origin/$BRANCH ..."
git push origin "$BRANCH"
