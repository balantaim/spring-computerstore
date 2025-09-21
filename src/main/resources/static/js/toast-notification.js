function notification() {
    const notyf = new Notyf({
        duration: 6000,
        ripple: false,
        position: {
            x: 'center',
            y: 'top',
        },
        types: [
            {
                type: 'success',
                //background: 'orange',
                icon:
                    {
                        className: 'material-icons',
                        tagName: 'i',
                        text: 'success'
                    }
            },
            {
                type: 'success',
                //background: 'indianred',
                duration: 2000,
                dismissible: true
            }
        ]
    });
    notyf.success("Text test");

//Custom notification (set icon: false)
//  notyf.open({ type: 'info',
//                message: 'Send us <b>an email</b> to get support'});

}

notification();

