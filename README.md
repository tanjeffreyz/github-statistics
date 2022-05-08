<h1 align="center">GitHub Statistics</h1>

Developed alongside [GitHub Overview](https://tanjeffreyz-github-overview.herokuapp.com/about),
GitHub Statistics automatically compiles both public and private user statistics that can then be 
displayed in a README:

<div align="center" width="100%">
  <a href="https://tanjeffreyz-github-overview.herokuapp.com/about">
    <img src="https://tanjeffreyz-github-overview.herokuapp.com/overview" width="100%" />
  </a>
</div>

<br>

In addition, GitHub Statistics also compiles repository information, which can be displayed using Markdown:

```
[![](https://tanjeffreyz-github-overview.herokuapp.com/repo/?r=1&c=0&maxR=4&owner=tanjeffreyz&repo=github-statistics)](https://github.com/tanjeffreyz/github-statistics)
```

<div align="center" width="100%">
  <a href="#">
    <img src="https://tanjeffreyz-github-overview.herokuapp.com/repo/?r=1&c=0&maxR=4&owner=tanjeffreyz&repo=github-statistics&delay=0" />
  </a>
</div>


## Features
### Live Statistics
The statistics are queried from GitHub's **GraphQL** API using a Java program, which automatically runs once every 
day via a scheduled GitHub Action workflow. Since GitHub Statistics has an access token (from an environment secret), it
can collect private statistics about the user as well as their repositories, allowing for a more accurate summary of
their contributions.


### Repository Info Cards
GitHub Statistics also compiles the repository information GitHub Overview needs to display **repository cards** like the one above.
Each repository's information is stored in its own JSON file under `/output/info`. GitHub Overview can later retrieve 
this information to render a repository card.
