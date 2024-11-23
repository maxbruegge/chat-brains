const fetchGitHubOctocat = async () => {
  const url = 'https://api.github.com/octocat';
  const token = 'YOUR-TOKEN';

  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
        'X-GitHub-Api-Version': '2022-11-28',
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json(); // Parse JSON response
    console.log(data); // Process the response data
  } catch (error) {
    console.error('Error fetching data:', error);
  }
};

// Call the function
fetchGitHubOctocat();
