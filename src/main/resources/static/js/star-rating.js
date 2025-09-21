const star = document.querySelectorAll('input');
const showValue = document.querySelector('#vote-number');
const textSuffix = document.querySelector('#vote-suffix');

for (let i = 0; i < star.length; i++) {
    star[i].addEventListener('click', function () {
        i = this.value;

        showValue.textContent = `${i}`;
        if (textSuffix.classList.contains('is-hidden')) {
            textSuffix.classList.remove('is-hidden');
        }
    });
}