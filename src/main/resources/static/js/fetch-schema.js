        const version = document.documentElement.lang ?? 'en';
        fetch(window.location.origin + `/other/schema_developer_${version}.json`)
          .then(response => response.text())
          .then(structuredDataText => {
            const script = document.createElement('script');
            script.setAttribute('type', 'application/ld+json');
            script.textContent = structuredDataText;
            document.head.appendChild(script);
          });